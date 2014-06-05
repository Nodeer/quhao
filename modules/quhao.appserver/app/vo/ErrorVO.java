package vo;

public class ErrorVO {
	public String key = "";
	public String cause = "";
	
	public ErrorVO(){
		
	}
	
	public ErrorVO(String key, String cause) {
		super();
		this.key = key;
		this.cause = cause;
	}
}
