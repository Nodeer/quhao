//
//  WithiterThirdViewController.h
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ASIHTTPRequest.h"

@interface WithiterThirdViewController : UIViewController <UITextFieldDelegate>{
    UILabel *mobileLabelForLogin;
    UITextField *mobileForLogin;    
    UILabel *passwordLabelForLogin;
    UITextField *passwordForLogin;
    UIButton *loginBTN;
    
    UILabel *mobileLabelForSignup;
    UITextField *mobileForSignup;
    UIButton *dynamicPwdBTN;
    UILabel *passwordLabelForSignup;
    UITextField *passwordForSignup;
    UIButton *signupBTN;
}

@property(nonatomic, retain) IBOutlet UILabel *mobileLabelForLogin;
@property(nonatomic, retain) IBOutlet UITextField *mobileForLogin;
@property(nonatomic, retain) IBOutlet UILabel *passwordLabelForLogin;
@property(nonatomic, retain) IBOutlet UITextField *passwordForLogin;
@property(nonatomic, retain) IBOutlet UIButton *loginBTN;

@property(nonatomic, retain) IBOutlet UILabel *mobileLabelForSignup;
@property(nonatomic, retain) IBOutlet UITextField *mobileForSignup;
@property(nonatomic, retain) IBOutlet UIButton *dynamicPwdBTN;
@property(nonatomic, retain) IBOutlet UILabel *passwordLabelForSignup;
@property(nonatomic, retain) IBOutlet UITextField *passwordForSignup;
@property(nonatomic, retain) IBOutlet UIButton *signupBTN;

@end
