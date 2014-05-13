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
#import "RegisterViewController.h"
#import "ForgetViewController.h"
@interface LoginView : UIViewController<UIWebViewDelegate>
{
    ASIFormDataRequest *_request;
}

@property (strong,nonatomic) Helper *helper;
@property (strong, nonatomic) IBOutlet UITextField *txt_Name;
@property (strong, nonatomic) IBOutlet UITextField *txt_Pwd;
@property (strong, nonatomic) IBOutlet UISwitch *switch_Remember;
@property BOOL _isPopupByNotice;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;

//登陆事件
- (IBAction)click_Login:(id)sender;
//注册事件
- (IBAction)registerAccount:(id)sender;
//键盘消失
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event;
//登陆失败事件
- (void)requestFailed:(ASIHTTPRequest *)requestNew;
//登陆成功事件
- (void)requestLogin:(ASIHTTPRequest *)requestNew;

@end
