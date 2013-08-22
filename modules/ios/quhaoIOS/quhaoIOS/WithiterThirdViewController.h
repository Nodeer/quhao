//
//  WithiterThirdViewController.h
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface WithiterThirdViewController : UIViewController <UITextFieldDelegate>{
    UILabel *mobileLabel;
    UILabel *passwordLabel;
    UITextField *mobile;
    UITextField *password;
    UIButton *signupBTN;
    UIButton *loginBTN;
}

@property(nonatomic, retain) IBOutlet UILabel *mobileLabel;
@property(nonatomic, retain) IBOutlet UILabel *passwordLabel;
@property(nonatomic, retain) IBOutlet UITextField *mobile;
@property(nonatomic, retain) IBOutlet UITextField *password;
@property(nonatomic, retain) IBOutlet UIButton *signupBTN;
@property(nonatomic, retain) IBOutlet UIButton *loginBTN;

@end
