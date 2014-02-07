//
//  ForgetViewController.m
//  quHaoIos
//
//  Created by sam on 13-10-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "ForgetViewController.h"

@interface ForgetViewController ()

@end

@implementation ForgetViewController

@synthesize accountField;
@synthesize mdField;
@synthesize passField;
@synthesize confirmPassField;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"获取验证码", @"获取验证码");
        self.tabBarItem.title = @"获取验证码";
        
    }
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor=[UIColor whiteColor ];
    
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    accountField = [[UITextField alloc] initWithFrame:CGRectMake(0, 5.0f, 320.0f, 40.0f)];
    [accountField setBorderStyle:UITextBorderStyleRoundedRect]; //外框类型
    accountField.placeholder = @"手机号码"; //默认显示的字
    accountField.secureTextEntry = NO; //是否以密码形式显示
    accountField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    accountField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    accountField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    accountField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    accountField.keyboardType = UIKeyboardTypeDefault;//键盘显示类型
    accountField.delegate = self;
    [self.view addSubview:accountField];
    
    mdField = [[UITextField alloc] initWithFrame:CGRectMake(0, 50.0f, 200.0f, 40.0f)];
    [mdField setBorderStyle:UITextBorderStyleRoundedRect]; //外框类型
    mdField.placeholder = @"验证码"; //默认显示的字
    mdField.secureTextEntry = NO; //是否以密码形式显示
    mdField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    mdField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    mdField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    mdField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    mdField.keyboardType = UIKeyboardTypeDefault;//键盘显示类型
    mdField.delegate = self;
    [self.view addSubview:mdField];
    
    CGRect frame = CGRectMake(205, 50, 100, 40);
    UIButton *mdButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    mdButton.frame=frame;
    [mdButton setTitle:@"获取验证码" forState:UIControlStateNormal];
    [mdButton addTarget:self action:@selector(getMd:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:mdButton];
    
    passField = [[UITextField alloc] initWithFrame:CGRectMake(0, 95.0f, 320.0f, 40.0f)];
    [passField setBorderStyle:UITextBorderStyleRoundedRect]; //外框类型
    passField.placeholder = @"新密码:6-14位字符"; //默认显示的字
    passField.secureTextEntry = YES; //是否以密码形式显示
    passField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    passField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    passField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    passField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    passField.keyboardType = UIKeyboardTypeDefault;//键盘显示类型
    passField.delegate = self;
    [self.view addSubview:passField];
    
    confirmPassField = [[UITextField alloc] initWithFrame:CGRectMake(0, 140.0f, 320.0f, 40.0f)];
    [confirmPassField setBorderStyle:UITextBorderStyleRoundedRect]; //外框类型
    confirmPassField.placeholder = @"确认密码:6-14位字符"; //默认显示的字
    confirmPassField.secureTextEntry = YES; //是否以密码形式显示
    confirmPassField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    confirmPassField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    confirmPassField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    confirmPassField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    confirmPassField.keyboardType = UIKeyboardTypeDefault;//键盘显示类型
    confirmPassField.delegate = self;
    [self.view addSubview:confirmPassField];
    
    CGRect frame2 = CGRectMake(10, 185, 300, 40);
    UIButton *zcButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    zcButton.frame=frame2;
    [zcButton setTitle:@"提交" forState:UIControlStateNormal];
    [zcButton addTarget:self action:@selector(resetPass:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:zcButton];
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}

//按下Done按钮的调用方法，我们让键盘消失
-(BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    [self.accountField resignFirstResponder];
    [self.mdField resignFirstResponder];
    
    return YES;
}
-(void)getMd:(id)sender
{   if(self.accountField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    NSString *urlStr=[NSString stringWithFormat:@"%@%@%@",[Helper getIp],getAuthCode_url,self.accountField.text];
    NSURL *url = [NSURL URLWithString:urlStr];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"数据错误，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
    
    // 解析Server端返回的JSON数据
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSArray *jsonObjects = [jsonParser objectWithString:response error:&error];
    
    if(!jsonObjects){
        NSString * errorText=[jsonObjects valueForKey:@"errorText"];
        
        if(![errorText isEqualToString:@""]){
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
        }else{
            
        }
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"数据错误，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
}

-(void)resetPass:(id)sender
{
    if(self.accountField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(self.mdField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入验证码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(self.passField.text.length==0){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入密码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if(self.passField.text.length<6||self.passField.text.length>14){
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"密码必须在6到14个字符" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    if (![self.passField.text isEqualToString:self.confirmPassField.text]) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"两次输入的密码不一样" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
        return;
    }
    NSString *urlStr=[NSString stringWithFormat:@"%@%@%@password=%@",[Helper getIp],updatePassCode_url,self.accountField.text,self.passField.text];
    NSURL *url = [NSURL URLWithString:urlStr];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"数据错误，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
    
    // 解析Server端返回的JSON数据
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSArray *jsonObjects = [jsonParser objectWithString:response error:&error];
    
    if(!jsonObjects){
        NSString * errorKey=[jsonObjects valueForKey:@"errorKey"];
        NSString * errorText=[jsonObjects valueForKey:@"errorText"];
        if([errorKey isEqualToString:@"0"]){
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
        }else{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
            [self.navigationController  popViewControllerAnimated:YES];
        }
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"数据错误，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }

}
//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.accountField resignFirstResponder];
    [self.mdField resignFirstResponder];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end

