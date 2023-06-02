using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Threading.Tasks;
using System.IO;
using WinSCP;
using System.Linq.Expressions;
using System.Net;
using static System.Collections.Specialized.BitVector32;
using System.Data.SqlClient;
using System.Runtime.Remoting.Contexts;
using System.Diagnostics;



/* *
 * Information has been redacted for security purposes
 * 
 * Author:  Sarah Hendricks
 * 
 * Purpous: Automate transfer of files from a local machiene to a remote FTP server via WinScp
 *          Logging all processes and storing the infomation in a MS SQL database
 *          
 * Library: WinSCP 
 * 
 * */




namespace WinSCPProject_1
{
	internal class Program
	{
		//Set Values using Settings
		static string LocalDir = Properties.Settings.Default.LocalDirectory;
		static string LocalArc = Properties.Settings.Default.LocalBackup;
		static string Remotdir = Properties.Settings.Default.RemoteDirectory;
		static string RemoteArc = Properties.Settings.Default.RemoteBackup;
		static string filenames = Properties.Settings.Default.Exports;
		static string infilenames = Properties.Settings.Default.Imports;
		static String Delim = Properties.Settings.Default.Delimiter;
		static DateTime dtFormat = DateTime.UtcNow;  // timestamp 





		static int Main(string[] args)
		{
			string logString = "";//init log string builder
			string logfile = Properties.Settings.Default.LogDirectory + "FTP AutomationErrorLog" + "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt";//initlog file

			//create sql connection
			SqlConnection SQLConnection = new SqlConnection();
			SQLConnection.ConnectionString = Properties.Settings.Default.SQLConnStr;

			//Build SQL log string
			string SQL = "";

			//open SQL connection
			SQLConnection.Open();

			SqlCommand cmdSQLLog = new SqlCommand(SQL, SQLConnection);
			writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""));

			dtFormat = DateTime.UtcNow;
			Console.WriteLine(dtFormat.ToString() + "   " + "SFTP Import Started... ");

			logString = " - Connecting to SFTP Server ";
			WriteLog(logString, logfile);
			writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""));


			if (!(Directory.Exists(Properties.Settings.Default.LogDirectory)))
			{

				Directory.CreateDirectory(Properties.Settings.Default.LogDirectory);

			}



			logString = " - FTP Automation Started... ";
			WriteLog(logString, logfile);
			writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""));


			try
			{
				// Setup session options
				SessionOptions sessionOptions = new SessionOptions
				{
					Protocol = Protocol.Ftp,
					HostName = "ftp.xxx.net",
					UserName = "xxx@xxxxxx.com",
					Password = "xxxxxx",
					
				};

				using (Session session = new Session())
				{
					// Connect
					session.Open(sessionOptions);

					// Upload files
					TransferOptions transferOptions = new TransferOptions();
					transferOptions.TransferMode = TransferMode.Binary;

					TransferOperationResult transferResult;




					string[] files = null;

					files = filenames.Split('|');




					switch (args[0])
					{
						case "1":   //upload from local to remote




							foreach (string file in files)
							{


								dtFormat = DateTime.UtcNow;
								Console.WriteLine(dtFormat.ToString() + "   " + "Processing File " + file);

								transferResult =
								session.GetFiles(Remotdir + "/" + file, LocalDir + "\\" + file, false, transferOptions);   //general files


								try
								{
									//Throw on any error
									transferResult.Check();

									//---------------------------------------------------------------// logging

									foreach (TransferEventArgs transfer in transferResult.Transfers)
									{
										if (!File.Exists(logfile))  //create the log if it does not exist
										{
											File.CreateText(logfile);
											logString = "- FTP imports " + Remotdir + file + " download to " + LocalDir + "\\" + file;
											WriteLog(logString, logfile);
											writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
											dtFormat = DateTime.UtcNow;
											Console.WriteLine(dtFormat.ToString() + "   " + logString);
										}
										else   //if the file does exist write to the log 
										{
											logString = "- FTP imports " + Remotdir + file + " downloaded to " + LocalDir + "\\" + file;
											WriteLog(logString, logfile);
											writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
											dtFormat = DateTime.UtcNow;
											Console.WriteLine(dtFormat.ToString() + "   " + logString);
										}
									}
								}
								catch (Exception e)
								{
									logString = "- FTP transport failed " + Remotdir + file + " Not Found! " + " -> " + e.Message;
									WriteLog(logString, logfile);
									writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 599);
									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + logString);

									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + "Error: {0}", e);
								}


								//Back up transfer
								transferResult =
								session.GetFiles(Remotdir + "/" + file, LocalArc + "\\" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt"), false, transferOptions);

								//throw on error
								try
								{
									transferResult.Check();

									foreach (TransferEventArgs transfer in transferResult.Transfers)
									{
										logString = "- FTP Import " + Remotdir + file + " downloaded to " + LocalArc + "\\" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt");
										WriteLog(logString, logfile);
										writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
										dtFormat = DateTime.UtcNow;
										Console.WriteLine(dtFormat.ToString() + "   " + logString);
									}

								}
								catch (Exception e)
								{
									logString = "- FTP transport failed " + Remotdir + file + " Not Found! " + " -> " + e.Message;
									WriteLog(logString, logfile);
									writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 599);

									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + logString);

									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + "Error: {0}", e);
								}

								session.MoveFile(Remotdir + "/" + file, RemoteArc + "/" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt"));
								logString = "- FTP Import File " + Remotdir + file + " Arcived " + "->" + RemoteArc + "/" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt");
								WriteLog(logString, logfile);
								writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
								dtFormat = DateTime.UtcNow;
								Console.WriteLine(dtFormat.ToString() + "   " + logString);
							}



							break;



						case "2":  //download from remote to local 
							foreach (string file in files)
							{



								transferResult =
								session.PutFiles(LocalDir + "\\" + file, Remotdir + "/" + file, false, transferOptions);   //general files


								try
								{
									//Throw on any error
									transferResult.Check();
									//---------------------------------------------------------------// logging errors for local 
									foreach (TransferEventArgs transfer in transferResult.Transfers)
									{
										if (!File.Exists(logfile))  //create the log if it does not exist
										{
											File.CreateText(logfile);
											logString = "- FTP Export " + LocalDir + "\\" + file + " uploaded to " + Remotdir + file;
											WriteLog(logString, logfile);
											writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
											dtFormat = DateTime.UtcNow;
											Console.WriteLine(dtFormat.ToString() + "   " + logString);
										}
										else   //if the file does exist write to the log 
										{
											logString = "- FTP Export " + LocalDir + "\\" + file + " uploaded to " + Remotdir + file;
											WriteLog(logString, logfile);
											writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
											dtFormat = DateTime.UtcNow;
											Console.WriteLine(dtFormat.ToString() + "   " + logString); ;
										}
									}
								}
								catch (Exception e)
								{
									logString = "- FTP transport failed " + LocalDir + "\\" + file + " Export Not Found! " + " -> " + e.Message;
									WriteLog(logString, logfile);
									writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 599);
									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + logString);

									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + "Error: {0}", e);
								}


								//Back up transfer
								transferResult =
								session.PutFiles(LocalArc + "\\" + file, Remotdir + "/" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt"), false, transferOptions);

								//throw on error
								try
								{
									transferResult.Check();

									foreach (TransferEventArgs transfer in transferResult.Transfers)
									{
										logString = "- FTP Export " + LocalDir + "\\" + file + " Uploaded to " + Remotdir + "/" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt");
										WriteLog(logString, logfile);
										writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
										dtFormat = DateTime.UtcNow;
										Console.WriteLine(dtFormat.ToString() + "   " + logString);
									}

								}
								catch (Exception e)
								{
									logString = "- FTP transport failed " + LocalDir + "\\" + file + " Export Not Found! " + " -> " + e.Message;
									WriteLog(logString, logfile);
									writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 599);
									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + logString);

									dtFormat = DateTime.UtcNow;
									Console.WriteLine(dtFormat.ToString() + "   " + "Error: {0}", e);

								}

								string stamp = DateTime.Now.ToString("yyyyMMddss");

								string toCopy = LocalDir + file;
								string dest = LocalArc;

								File.Copy(toCopy, dest + string.Concat(Path.GetFileNameWithoutExtension(file), //copy general files to local backup
											DateTime.Now.ToString("yyyyMMddHHmmssfff"),
											Path.GetExtension(file)));
								//session.MoveFile(Remotdir + "/" + file, RemoteArc + "/" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt"));
								logString = " - FTP Export File " + LocalDir + "\\" + file + " moved to " + LocalArc + "\\" + file.Replace(".txt", "-" + DateTime.UtcNow.ToString("yyyyMMddHHmmss") + ".txt");
								WriteLog(logString, logfile);
								writeSQLLog(cmdSQLLog, logString.Replace(" - ", ""), 0);
								dtFormat = DateTime.UtcNow;
								Console.WriteLine(dtFormat.ToString() + "   " + logString);
							}



							break;



							/*

							foreach (string file in files)
							{
								transferResult =
								session.GetFiles(remotdir + file, localDir, false, transferOptions);
								//--------------------------------------------------------------------- //backing up to local and remote locations with timestamp appended

								string stamp = DateTime.Now.ToString("yyyyMMddss");

								string toCopy = localDir + file;
								string dest = @"E:\C#\BackupLocal\";

								File.Copy(toCopy, dest + string.Concat(Path.GetFileNameWithoutExtension(file), //copy general files to local backup
											DateTime.Now.ToString("yyyyMMddHHmmssfff"),
											Path.GetExtension(file)));

								session.PutFiles(@"E:\C#\BackupLocal\*", "/SHWinSCPFilesRemote/BackupRemote/", false, transferOptions); //copy of files to remote back up

								//---------------------------------------------------------------// logging errors for remote 

								//Throw on any error
								transferResult.Check();

								foreach (TransferEventArgs transfer in transferResult.Transfers)
								{

									

									
									if (!session.FileExists(path2))  //create the log if it does not exist
									{
										

										using (StreamWriter writetext = File.AppendText(path3))//adds to end of file

										{


											DateTime dtFormat = DateTime.UtcNow; // timestamp + file name

											writetext.WriteLine(dtFormat.ToString() + " " + "Upload of {0} succeeded" + transfer.FileName);

											session.PutFiles(path3, "/SHWinSCPFilesRemote/RemoteLogs/",false, transferOptions);
										}


									}
									else
									{
										session.GetFiles(path2, @"E:\C#\TempLogs", false, transferOptions); // reterve the remote log

										using (StreamWriter writetext = File.AppendText(path3))  //add to the remote log

										{

											DateTime dtFormat = DateTime.UtcNow;


											writetext.WriteLine(dtFormat.ToString() + " " + "Upload succeeded" + transfer.FileName);

											session.PutFiles(path3, "/SHWinSCPFilesRemote/RemoteLogs/", false, transferOptions); //upload the remote log
										}

									}


								}


							}
							

							break;
							*/
					}
					session.Close();
					session.Dispose();
				}



				return 0;


			}
			catch (Exception e)
			{


				logString = " - FTP ERROR: " + e.Message;
				WriteLog(logString, logfile);


				Console.WriteLine("Error: {0}", e);
				return 1;
			}
		}

		private static void writeSQLLog(SqlCommand cmdSQLLog, string logText, int errNum = 0)
		{
			string Sql = "insert into LOGS.dbo.tblSQLLog(logMsg, errNum) select '" + logText.Replace("'", "''") + "', " + errNum.ToString();
			cmdSQLLog.CommandText = Sql;
			cmdSQLLog.ExecuteNonQuery();
		}

		public static void WriteLog(string logtext, string logfile)
		{
			using (StreamWriter writetext = File.AppendText(logfile))
			{
				DateTime dtFormat = DateTime.UtcNow;
				writetext.WriteLine(dtFormat.ToString() + " " + logtext);

			}
		}
	}
}
