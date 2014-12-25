package me.capit.urbanization.aegis;

public class AegisDisabledException extends AegisException {
	private static final long serialVersionUID = 8530137312087116822L;

	public AegisDisabledException(){
		super("This feature is not available: the &e&oAegis&r system is disabled.");
	}

}
