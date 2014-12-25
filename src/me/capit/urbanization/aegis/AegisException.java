package me.capit.urbanization.aegis;

public class AegisException extends Exception {
	private static final long serialVersionUID = -2621296268286175661L;

	public AegisException(String msg){
		super(msg);
	}
	
	public AegisException(){
		super("Failed to load city.");
	}
}
