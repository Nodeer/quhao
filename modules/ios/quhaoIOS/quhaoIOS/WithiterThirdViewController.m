//
//  WithiterThirdViewController.m
//  quhaoIOS
//
//  Created by cross on 13-7-21.
//  Copyright (c) 2013年 withiter. All rights reserved.
//

#import "WithiterThirdViewController.h"
#import "SBJson.h"

@interface WithiterThirdViewController ()

@end

@implementation WithiterThirdViewController

@synthesize mobileLabel;
@synthesize passwordLabel;
@synthesize mobile;
@synthesize password;
@synthesize signupBTN;
@synthesize loginBTN;

//UITextField的协议方法，当开始编辑时监听
-(BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    NSTimeInterval animationDuration=0.30f;
    [UIView beginAnimations:@"ResizeForKeyboard" context:nil];
    [UIView setAnimationDuration:animationDuration];
    float width = self.view.frame.size.width;
    float height = self.view.frame.size.height;
    //上移30个单位，按实际情况设置
    CGRect rect=CGRectMake(0.0f,-30,width,height);
    self.view.frame=rect;
    [UIView commitAnimations];
    return YES;
}

//恢复原始视图位置
-(void)resumeView
{
    NSTimeInterval animationDuration=0.30f;
    [UIView beginAnimations:@"ResizeForKeyboard" context:nil];
    [UIView setAnimationDuration:animationDuration];
    float width = self.view.frame.size.width;
    float height = self.view.frame.size.height;
    //如果当前View是父视图，则Y为20个像素高度，如果当前View为其他View的子视图，则动态调节Y的高度
    float Y = 20.0f;
    CGRect rect=CGRectMake(0.0f,Y,width,height);
    self.view.frame=rect;
    [UIView commitAnimations];
}

//隐藏键盘的方法
-(void)hidenKeyboard
{
    [self.mobile resignFirstResponder];
    [self.password resignFirstResponder];
    [self resumeView];
}

//点击键盘上的Return按钮响应的方法
-(IBAction)nextOnKeyboard:(UITextField *)sender
{
    if (sender == self.mobile) {
        [self.password becomeFirstResponder];
    }else if (sender == self.password){
        [self hidenKeyboard];
    }
}

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"个人中心", @"个人中心");
        self.tabBarItem.image = [UIImage imageNamed:@"first"];
    }
    return self;
}

/*
 * Sign up click function
 */
-(IBAction)onClickSignUpBTN:(id)sender{
    UIAlertView *alert =
    [[UIAlertView alloc] initWithTitle:@"Alert" message:@"注册!" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

/**
 * Login click function
 */
-(IBAction)onClickLoginBTN:(id)sender{
    NSString *mobileValue = mobile.text;
    NSString *passwordValue = password.text;
    
    NSLog(@"mobile is : %@", mobileValue);
    NSLog(@"password is : %@", passwordValue);
    
    NSString *urlStr = [NSString stringWithFormat:@"http://192.168.1.20:9081/login?phone=%@&password=%@",mobileValue, passwordValue];
    NSURL *url = [NSURL URLWithString:urlStr];
    ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
    [request startSynchronous];
    NSError *httpError = [request error];
    NSString *response = @"";
    if (!httpError) {
        response = [request responseString];
        NSLog(@"%@", response);
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message: @"网络不是很好，请稍后再试" delegate:self cancelButtonTitle:@"好的" otherButtonTitles:nil, nil];
        [alert show];
    }

    
    
    UIAlertView *alert =
    [[UIAlertView alloc] initWithTitle:@"Alert" message:response delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //指定本身为代理
    self.mobile.delegate = self;
    self.password.delegate = self;
    //指定编辑时键盘的return键类型
    self.mobile.returnKeyType = UIReturnKeyNext;
    self.password.returnKeyType = UIReturnKeyDefault;
    
    //注册键盘响应事件方法
    [self.mobile addTarget:self action:@selector(nextOnKeyboard:) forControlEvents:UIControlEventEditingDidEndOnExit];
    [self.password addTarget:self action:@selector(nextOnKeyboard:) forControlEvents:UIControlEventEditingDidEndOnExit];
    
    //添加手势，点击屏幕其他区域关闭键盘的操作
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(hidenKeyboard)];
    gesture.numberOfTapsRequired = 1;
    [self.view addGestureRecognizer:gesture];
    
    [loginBTN addTarget:self action:@selector(onClickLoginBTN:) forControlEvents:UIControlEventTouchUpInside];
    [signupBTN addTarget:self action:@selector(onClickSignUpBTN:) forControlEvents:UIControlEventTouchUpInside];
    
    
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
