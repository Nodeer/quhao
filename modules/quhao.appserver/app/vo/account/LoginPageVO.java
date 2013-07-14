package vo.account;

public class LoginPageVO {

	public String errorField;
	public String errorText;
	
	public LoginPageVO(){
		super();
	}
	
	public LoginPageVO(String errorField, String errorText) {
		super();
		this.errorField = errorField;
		this.errorText = errorText;
	}
}
