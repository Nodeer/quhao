package vo.account;

import org.bson.types.ObjectId;

public class EditProfilePageVO {

	public String firstName;
	public String lastName;
	public String role;
	public String email;
	public String applicationType;
	public String location;
	
	public String errorKey;
	public String errorText;
	
	public boolean imageExist;
	public boolean facebookImageExist;
	public String facabookImage;
	
	public boolean facebookExist;
	public ObjectId facebookObjId;
	public String facebookId = "";
	public String facebookImage = "";
	public String facebookAccessToken = "";
	public String facebookFirstName = "";
	public String facebookLastName = "";
	public String facebookDisplayName = "";
	
}
