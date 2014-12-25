package me.capit.urbanization.city;

public class CityException extends Exception {
	private static final long serialVersionUID = -4358775010108315534L;
	
	public CityException(String msg){
		super(msg);
	}
	
	public CityException(){
		super("Failed to load city.");
	}
}
