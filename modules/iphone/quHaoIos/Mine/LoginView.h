//
//  LoginView.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <UIKit/UIKit.h>
#import "Helper.h"
#import "ApiError.h"
#import "MineViewController.h"
#import "SBJson.h"
#import "RegisterController.h"
#import "ForgetViewController.h"
@interface LoginView : UIViewController<UIWebViewDelegate> 
{
    ASIFormDataRequest *request;
}
@property (strong,nonatomic) Helper *helper;

@property (strong, nonatomic) IBOutlet UITextField *txt_Name;
@property (strong, nonatomic) IBOutlet UITextField *txt_Pwd;
@property (strong, nonatomic) IBOutlet UISwitch *switch_Remember;
@property BOOL isPopupByNotice;

- (IBAction)click_Login:(id)sender;
- (IBAction)textEnd:(id)sender;
- (IBAction)backgrondTouch:(id)sender;
- (void)analyseUserInfo:(NSString *)str;
- (void)registerAccount:(id)sender;
- (void)requestFailed:(ASIHTTPRequest *)requestNew;
- (void)requestLogin:(ASIHTTPRequest *)requestNew;
@end
