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
@synthesize isPopupByNotice;
@synthesize helper;


#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    //helper=[Helper new];
    self.navigationItem.title = @"登录";
    //决定是否显示用户名以及密码
    if([[Helper returnUserString:@"autoLogin"] boolValue]){
        NSString *name = helper.getUserName;
        NSString *pwd = helper.getPwd;
        if (name && ![name isEqualToString:@""]) {
            self.txt_Name.text = name;
        }
        if (pwd && ![pwd isEqualToString:@""]) {
            self.txt_Pwd.text = pwd;
        }
    }
        
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 忘记密码" rect:CGRectMake( 0, 0, 70, 35 )];
    [btnButton addTarget:self action:@selector(forgetMd:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
    
    
    CGRect frame = CGRectMake(60, 180, 90, 40);
    UIButton *zcButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    zcButton.frame=frame;
    [zcButton setTitle:@"注 册" forState:UIControlStateNormal];
    [zcButton addTarget:self action:@selector(registerAccount:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:zcButton];
    
    CGRect frame2 = CGRectMake(170, 180, 90, 40);
    UIButton *dlButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    dlButton.frame=frame2;
    [dlButton setTitle:@"登 录" forState:UIControlStateNormal];
    [dlButton addTarget:self action:@selector(click_Login:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:dlButton];
    
}

- (void)viewDidUnload
{
    [self setTxt_Name:nil];
    [self setTxt_Pwd:nil];
    [self setSwitch_Remember:nil];
   // [Helper CancelRequest:request];
    //request = nil;

    [super viewDidUnload];
}
- (void)viewDidDisappear:(BOOL)animated
{  
  //  [Helper CancelRequest:request];

}
- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)registerAccount:(id)sender
{
    RegisterController *reg = [[RegisterController alloc] init];
    reg.title = @"注 册";
    [self.navigationController pushViewController:reg animated:YES];
}
- (IBAction)click_Login:(id)sender
{
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
    NSString *name =self.txt_Name.text;
    NSString *pwd = self.txt_Pwd.text;
    NSString *urlStr=[NSString stringWithFormat:@"%@%@",[Helper getIp],@"/login"];
     request = [ASIFormDataRequest requestWithURL:[NSURL URLWithString:urlStr]];
    [request setUseCookiePersistence:YES];
    [request setPostValue:name forKey:@"phone"];
    [request setPostValue:pwd  forKey:@"password"];
    [request setPostValue:@"1" forKey:@"keep_login"];
    [request setDelegate:self];
    [request setDidFailSelector:@selector(requestFailed:)];
    [request setDidFinishSelector:@selector(requestLogin:)];
    [request startAsynchronous];
    
    request.hud = [[MBProgressHUD alloc] initWithView:self.view];
    [Helper showHUD:@"正在登录" andView:self.view andHUD:request.hud];
}
- (void)requestFailed:(ASIHTTPRequest *)requestNew
{
    if (requestNew.hud) {
        [requestNew.hud hide:YES];
    }
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
            [helper saveCookie:YES];
            
            NSNumber *convertSwitchStatus=[[NSNumber alloc] initWithBool:self.switch_Remember.isOn];
            [[NSUserDefaults standardUserDefaults] setObject:convertSwitchStatus forKey:@"autoLogin"];
            [[NSUserDefaults standardUserDefaults] synchronize];

            if (isPopupByNotice == NO)
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
                [helper saveUserNameAndPwd:@"" andPwd:@""];
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
            if (self.isPopupByNotice) {
                [self.navigationController popViewControllerAnimated:YES];
            }
            //[[MyThread Instance] startNotice];
        }
            break;
        case -1:
        {
            [Helper ToastNotification:[NSString stringWithFormat:@"%@",@"帐号不存在,请重新输入。"] andView:self.view andLoading:NO andIsBottom:NO];
        }
            break;
        case -2:
        {
            [Helper ToastNotification:[NSString stringWithFormat:@"%@",@"密码错误,请重新输入。"] andView:self.view andLoading:NO andIsBottom:NO];
        }
            break;
    }
}

- (IBAction)textEnd:(id)sender
{
    [sender resignFirstResponder];
}

- (IBAction)backgrondTouch:(id)sender
{
    [self.txt_Pwd resignFirstResponder];
    [self.txt_Name resignFirstResponder];
}


- (void)analyseUserInfo:(NSString *)xml
{
//    @try {
//        TBXML *_xml = [[TBXML alloc] initWithXMLString:xml error:nil];
//        TBXMLElement *root = _xml.rootXMLElement;
//        TBXMLElement *user = [TBXML childElementNamed:@"user" parentElement:root];
//        TBXMLElement *uid = [TBXML childElementNamed:@"uid" parentElement:user];
//        //获取uid
//        [[Config Instance] saveUID:[[TBXML textForElement:uid] intValue]];
//    }
//    @catch (NSException *exception) {
//        [NdUncaughtExceptionHandler TakeException:exception];
//    }
//    @finally {
//        
//    }
    
}

- (void)forgetMd:(id)sender
{
    ForgetViewController *reg = [[ForgetViewController alloc] init];
    reg.title = @"忘记密码";
    [self.navigationController pushViewController:reg animated:YES];
}

- (void)dealloc
{
    //[Helper CancelRequest:request];
   // [request clearDelegatesAndCancel];
    request=nil;
}
@end
