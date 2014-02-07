//
//  RegisterController.m
//  quHaoIos
//
//  Created by sam on 13-10-27.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "RegisterController.h"

@interface RegisterController ()

@end

@implementation RegisterController
@synthesize accountField;
@synthesize mdField;
@synthesize passField;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"注册", @"注册");
        self.tabBarItem.title = @"注册";
        
    }
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor=[UIColor whiteColor ];
    
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
    passField.placeholder = @"密码:6-14位字符"; //默认显示的字
    passField.secureTextEntry = YES; //是否以密码形式显示
    passField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    passField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    passField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    passField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    passField.keyboardType = UIKeyboardTypeDefault;//键盘显示类型
    passField.delegate = self;
    [self.view addSubview:passField];
    
    CGRect frame2 = CGRectMake(10, 140, 300, 40);
    UIButton *zcButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    zcButton.frame=frame2;
    [zcButton setTitle:@"注 册" forState:UIControlStateNormal];
    
    [zcButton addTarget:self action:@selector(addAccount:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:zcButton];
}

//按下Done按钮的调用方法，我们让键盘消失
-(BOOL)textFieldShouldReturn:(UITextField *)textField{
    
    [self.accountField resignFirstResponder];
    [self.passField resignFirstResponder];
    [self.mdField resignFirstResponder];

    return YES;
}
-(void)getMd:(id)sender
{  

    if(self.accountField.text.length!=0){
        if(self.accountField.text.length==11){
            NSString *urlStr=[NSString stringWithFormat:@"%@%@%@",[Helper getIp],authCode_url,self.accountField.text];
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

            if(jsonObjects!=nil){
                NSString * errorText=[jsonObjects valueForKey:@"errorText"];
                
                if(errorText.length!=0){
                    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                    [alert show];
                }else{
                    
                }
            }else{
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"数据错误，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                [alert show];
            }
        }else{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请输入正确的手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
        }
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"请先输入手机号码" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }
}

-(void)addAccount:(id)sender
{   if(self.accountField.text.length==0){
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
    if([Helper isConnectionAvailable]){
        
        NSString *urlStr=[NSString stringWithFormat:@"%@%@%@&code=%@&password=%@",[Helper getIp],register_url,self.accountField.text,self.mdField.text,self.passField.text];
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
        
        if(jsonObjects!=nil){
            NSString * errorText=[jsonObjects valueForKey:@"errorText"];
            NSString * errorKey=[jsonObjects valueForKey:@"errorKey"];

            if(![errorKey isEqualToString:@"1"]){
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                [alert show];
            }else{
                UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: errorText delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
                [alert show];
                [self clickToMine];
            }
        }else{
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"数据错误，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
            [alert show];
        }
    }else{
        MBProgressHUD *hud = [MBProgressHUD showHUDAddedTo:self.view animated:YES];
        hud.removeFromSuperViewOnHide =YES;
        //hud.mode = MBProgressHUDModeText;
        hud.labelText = NSLocalizedString(@"当前网络不可用,请检查网络链接", nil);
        hud.minSize = CGSizeMake(132.f, 108.0f);
        [hud hide:YES afterDelay:1];
    }
}
//点击屏幕空白处去掉键盘
- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.accountField resignFirstResponder];
    [self.passField resignFirstResponder];
    [self.mdField resignFirstResponder];
}

- (void)clickToMine
{
    [self.navigationController  popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end

