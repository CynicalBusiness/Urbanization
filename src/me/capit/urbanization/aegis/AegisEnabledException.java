package me.capit.urbanization.aegis;

public class AegisEnabledException extends AegisException {
	private static final long serialVersionUID = -4487789318165898802L;
	
	public AegisEnabledException(){
		super("This feature is not available: the &e&oAegis&r system is enabled instead.");
	}

}
