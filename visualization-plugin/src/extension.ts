import { configure, getLogger } from 'log4js';
import * as vscode from 'vscode';
import { IveUri } from './visualisation/IveUri';
import { VisualizationDependency } from "./visualisation/VisualizationDependency"


export function activate(context: vscode.ExtensionContext) {

	// Use the console to output diagnostic information (console.log) and errors (console.error)
	// This line of code will only be executed once when your extension is activated
	console.log('Congratulations, your extension "sysmlvisualisation" is now active!');
  setupLog(context.logUri, "viz.log");

	let version = vscode.workspace.getConfiguration("sysmlvisualisation.dependencies").get<string>("version")!!;
	let binPath = IveUri.appendUri(context.extensionUri, "bin");
	let libPath = IveUri.appendUri(context.extensionUri, "lib");
	let tempPath = context.storageUri? context.storageUri : context.logUri
	let visualisation = new VisualizationDependency("visualization",
														version,
														binPath,
														libPath,
														tempPath)
	if (!visualisation.usesExternalLibrary()) {
		visualisation.unpackDefaultLibrary();
		};

  let disposable = vscode.commands.registerCommand('sysmlvisualisation.visualization.sysml', async (uri: vscode.Uri) => {
	vscode.window.withProgress(
		{ location: vscode.ProgressLocation.Notification, title: "Generating Visualisation" }, async () =>
			await visualisation.visualize(uri))
			.then(svgPath => vscode.commands.executeCommand("_svg.showSvgByUri", svgPath));
  });

  vscode.commands.registerCommand("sysmlvisualisation.visualization.dispose", (uri: vscode.Uri) => {
    visualisation.dispose;
  });

  context.subscriptions.push(disposable);

  /**
 * Erstellt einen Logger, der gleichzeitig in die Ausgabe des Hosts (Debugging oder Start aus
 * Konsole), Ausgabe des Clients (Output-Window in IVE), in ein File und optional in die
 * Notifications des Clients loggen kann.
 * Zugriff auf den Logger mit getLogger() bzw. getLogger("user") für die User-Notifications.
 */
function setupLog(logPath: vscode.Uri, logName: string) {
  // Logging window in the client, i.e., in IVE
  const clientConsole = vscode.window.createOutputChannel("Log (Ive)");

  // Location of log file
  const logFile = IveUri.appendUri(logPath, logName).fsPath;
  const logFileForAS = IveUri.appendUri(logPath, 'Autosledgi.log').fsPath;

  // An appender that allows the use of a "custom" function to alter logging behavior
  const customizableAppender = {
    configure: (config: any, layouts: any, findAppender: any, levels: any) => {
      return (loggingEvent: any) => {
        config.custom(loggingEvent, layouts);
      };
    }
  };

  configure({
    appenders: {
      // Host console, i.e., VSCode or Terminal
      host: { type: 'console' },
      // File
      file: { type: 'file', filename: logFile, maxLogSize: 1000000, backups: 1 },
      // File for Autosledgi
      asLogFile: { type: 'file', filename: logFileForAS, maxLogSize: 1000000, backups: 1 },
      // Client console, i.e., console in IVE
      app: {
        type: customizableAppender,
        custom: (logEvent: any, layouts: any) => clientConsole.appendLine(layouts.basicLayout(logEvent))
      },
      // Shows messages boxes (notifications) in client, i.e., notifications in IVE
      info: {
        type: customizableAppender,
        custom: (loggingEvent: any, layouts: any) => {
          vscode.window.showInformationMessage(layouts.patternLayout("%m")(loggingEvent));
        }
      },
      warn: {
        type: customizableAppender,
        custom: (loggingEvent: any, layouts: any) => {
          vscode.window.showWarningMessage(layouts.patternLayout("%m")(loggingEvent));
        }
      },
      error: {
        type: customizableAppender,
        custom: (loggingEvent: any, layouts: any) => {
          vscode.window.showWarningMessage(layouts.patternLayout("%m")(loggingEvent));
        }
      },
      // Wrapper for notifications, filters specific level
      infoNotification: { type: 'logLevelFilter', appender: 'info', level: 'info', maxLevel: 'info' },
      warnNotification: { type: 'logLevelFilter', appender: 'warn', level: 'warn', maxLevel: 'warn' },
      errorNotification: { type: 'logLevelFilter', appender: 'error', level: 'error', maxLevel: 'error' }
    },
    categories: {
      // Default logger
      default: { appenders: ['host', 'file', 'app'], level: 'trace' },
      // Promts the user with notifications
      user: { appenders: ['host', 'file', 'app', 'infoNotification', 'warnNotification', 'errorNotification'], level: 'trace' },
    }
  });

  getLogger().debug("Logger created! Will write to " + logFile);
}
}

export function deactivate() {
  vscode.commands.executeCommand("sysmlvisualisation.visualization.dispose");
}
