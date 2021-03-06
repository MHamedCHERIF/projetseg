package aoUrnToRam.javaExternalCall;

import fr.irisa.triskell.kermeta.runtime.RuntimeObject;
import fr.irisa.triskell.kermeta.runtime.basetypes.String;

public class CommandLine {
	public static RuntimeObject exe(RuntimeObject command,RuntimeObject input){
		try{
			java.lang.String result=CommandLineImpl.exe(String.getValue(command),String.getValue(input));
			return String.create(result,command.getFactory());
		}
		catch(Exception ex){
			Logger.logError(ex);
			return String.create(null,command.getFactory());
		}
	}
}
