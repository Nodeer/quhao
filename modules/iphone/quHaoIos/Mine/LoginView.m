//
//  LoginView.m
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import "LoginView.h"

@implementation LoginView
@synthesize txt_Name;
@synthesize txt_Pwd;
@synthesize switch_Remember;
@synthesize _isPopupByNotice;
@synthesize helper;

#pragma mark - View lifecycle
- (void)viewDidLoad
{
    [super viewDidLoad];
    [self.scrollView setContentSize:CGSizeMake(kDeviceWidth, 480)];
    self.navigationItem.title = @"登录";
    //决定是否显示用户名以及密码
    if([[Helper returnUserString:@"autoLogin"] boolValue]){
        NSString *name = [Helper getUserName];
        NSString *pwd = [Helper getPwd];
        if (name && ![name isEqualToString:@""]) {
            self.txt_Name.text = name;
        }
        if (pwd && ![pwd isEqualToString:@""]) {
            self.txt_Pwd.text = pwd;
        }
    }
    
    
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton = [Helper getBtn:@"忘记密码" rect:CGRectMake( 0, 0, 60, 25 )];
    [btnButton addTarget:self action:@selector(forgetMd:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (IBAction)click_Login:(id)sender {
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    UIButton *la=(UIButton *)tap;
    la.enabled = NO;
    if(self.txt_Name.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(self.txt_Pwd.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入密码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    [self.view endEditing:YES];
    if(![Helper isConnectionAvailable]){
        [Helper ToastNotification:[NSString stringWithFormat:@"%@",@"当前网络不可用"] andView:self.view andLoading:NO andIsBottom:NO];
        la.enabled = YES;
        return;
    }
    NSString *name =self.txt_Name.text;
    NSString *pwd = self.txt_Pwd.text;
    NSString *urlStr=[NSString stringWithFormat:@"%@%@",IP,@"/login"];
    _request = [ASIFormDataRequest requestWithURL:[NSURL URLWithString:urlStr]];
    [_request setUseCookiePersistence:YES];
    [_request setPostValue:name forKey:@"phone"];
    [_request setPostValue:pwd  forKey:@"password"];
    [_request setPostValue:@"1" forKey:@"keep_login"];
    [_request setDelegate:self];
    [_request setDidFailSelector:@selector(requestFailed:)];
    [_request setDidFinishSelector:@selector(requestLogin:)];
    [_request startAsynchronous];
    la.enabled = YES;

    _request.hud = [[MBProgressHUD alloc] initWithView:self.view];
    [Helper showHUD:@"正在登录" andView:self.view andHUD:_request.hud];
}

- (IBAction)registerAccount:(id)sender {
    RegisterViewController *reg = [[RegisterViewController alloc] init];
    reg.title = @"注 册";
    [self.navigationController pushViewController:reg animated:YES];
}

- (void)requestFailed:(ASIHTTPRequest *)requestNew
{
    if (requestNew.hud) {
        [requestNew.hud hide:YES];
    }
    [Helper ToastNotification:[NSString stringWithFormat:@"%@",@"登录失败,请稍候再试"] andView:self.view andLoading:NO andIsBottom:NO];
}

- (void)requestLogin:(ASIHTTPRequest *)requestNew
{
    if (requestNew.hud) {
        [requestNew.hud hide:YES];
    }
    [Helper getUserNotice:requestNew];
    [requestNew setUseCookiePersistence:YES];
    ApiError *error = [Helper getApiError:requestNew];
    
    if (error == nil) {
        [Helper ToastNotification:requestNew.responseString andView:self.view andLoading:NO andIsBottom:NO];
    }
    switch (error.errorCode) {
            
        case 0:
        {
            [Helper saveCookie:YES];
            
            NSNumber *convertSwitchStatus=[[NSNumber alloc] initWithBool:self.switch_Remember.isOn];
            [[NSUserDefaults standardUserDefaults] setObject:convertSwitchStatus forKey:@"autoLogin"];
            [[NSUserDefaults standardUserDefaults] synchronize];

            if (_isPopupByNotice == NO)
            {
                [self.navigationController popViewControllerAnimated:YES];
            }
            
            //处理是否记住用户名或者密码
            if (self.switch_Remember.isOn)
            {
                [helper saveUserNameAndPwd:self.txt_Name.text andPwd:self.txt_Pwd.text];
            }
            //否则需要清空用户名于密码
            else
            {
                [helper saveUserNameAndPwd:self.txt_Name.text andPwd:@""];
            }
            //返回的处理
            
            if (helper.viewBeforeLogin)
            {
                if([helper.viewNameBeforeLogin isEqualToString:@"MineViewController"])
                {
                    MineViewController *_parent = (MineViewController *)helper.viewBeforeLogin;
                    _parent.isLoginJustNow = YES;
                }
            }
            
            //开始分析 uid 等等信息
            //[self analyseUserInfo:requestNew.responseString];
            
            //分析是否需要退回
            if (_isPopupByNotice) {
                [self.navigationController popViewControllerAnimated:YES];
            }
            //[[MyThread Instance] startNotice];
        }
            break;
        case -1:
        {
            [Helper ToastNotification:[NSString stringWithFormat:@"%@",@"帐号不存在,请重新输入"] andView:self.view andLoading:NO andIsBottom:NO];
        }
            break;
        case -2:
        {
            [Helper ToastNotification:[NSString stringWithFormat:@"%@",@"密码错误,请重新输入"] andView:self.view andLoading:NO andIsBottom:NO];
        }
            break;
    }
}

//- (IBAction)textEnd:(id)sender
//{
//    [sender resignFirstResponder];
//}

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.view endEditing:YES];
}

- (void)forgetMd:(id)sender
{
    ForgetViewController *reg = [[ForgetViewController alloc] init];
    reg.title = @"忘记密码";
    [self.navigationController pushViewController:reg animated:YES];
}
@end
