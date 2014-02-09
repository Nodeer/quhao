//
//  FeedbackViewController.m
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "FeedbackViewController.h"

@interface FeedbackViewController ()

@end

@implementation FeedbackViewController

@synthesize accountField;
@synthesize textView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"意见反馈", @"意见反馈");
        self.tabBarItem.title = @"意见反馈";
        
    }
    return self;
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor whiteColor];

    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 7, 50, 35 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 提 交" rect:CGRectMake( 0, 7, 50, 30 )];
    [btnButton addTarget:self action:@selector(updateXx:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
    
    UILabel *textLabel = [Helper getCustomLabel:@"欢迎您提出宝贵的意见，以便我们更好的改善软件。" font:16 rect:CGRectMake(5, 5.0f, 310.0f, 60.0f)];
    textLabel.lineBreakMode = NSLineBreakByWordWrapping;
    textLabel.numberOfLines=2;
    [self.view addSubview:textLabel];
    
    UILabel *yjLabel = [Helper getCustomLabel:@"意  见" font:16 rect:CGRectMake(5, 55.0f, 310.0f, 30.0f)];
    [self.view addSubview:yjLabel];

    self.textView = [[UITextView alloc] initWithFrame:CGRectMake(5, 90, 310, 120)];
    self.textView.layer.borderColor = UIColor.grayColor.CGColor;
    self.textView.layer.borderWidth = 1;
    self.textView.layer.cornerRadius = 6.0;
    self.textView.layer.masksToBounds = YES;
    self.textView.clipsToBounds = YES;
    self.textView.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    self.textView.delegate = self;//设置它的委托方法
    self.textView.backgroundColor = [UIColor whiteColor];//设置它的背景颜色
    self.textView.text = @"";//设置它显示的内容
    self.textView.returnKeyType = UIReturnKeyDefault;//返回键的类型
    self.textView.keyboardType = UIKeyboardTypeDefault;//键盘类型
    self.textView.scrollEnabled = YES;//是否可以拖动
    self.textView.autoresizingMask = UIViewAutoresizingFlexibleHeight;//自适应高度
    [self.view addSubview: self.textView];
    
    UILabel *sjLabel = [Helper getCustomLabel:@"手机号码" font:16 rect:CGRectMake(5, 180.0f, 320.0f, 20.0f)];
    [self.view addSubview:sjLabel];
    
    accountField = [[UITextField alloc] initWithFrame:CGRectMake(5, 205.0f, 310.0f, 31.0f)];
    accountField.placeholder = @""; //默认显示的字
    accountField.layer.borderColor = UIColor.grayColor.CGColor;
    accountField.layer.borderWidth = 1;
    accountField.layer.cornerRadius = 6.0;
    accountField.layer.masksToBounds = YES;
    accountField.clipsToBounds = YES;
    accountField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    accountField.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    accountField.autocorrectionType = UITextAutocorrectionTypeNo;//设置是否启动自动提醒更正功能
    accountField.returnKeyType = UIReturnKeyDone;  //键盘返回类型
    accountField.clearButtonMode = UITextFieldViewModeWhileEditing; //编辑时会出现个修改X
    accountField.keyboardType = UIKeyboardTypeNumberPad;//键盘显示类型
    accountField.delegate = self;
    accountField.backgroundColor=[UIColor whiteColor];
    [self.view addSubview:accountField];
    
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.accountField resignFirstResponder];
    [self.textView resignFirstResponder];
}

#pragma mark - 调整输入框与关闭键盘  TextView
- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y-90, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (BOOL)textViewShouldEndEditing:(UITextView *)textView
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y+90, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

#pragma mark - 调整输入框与关闭键盘  TextField
- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y-80, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    
    return YES;
}

- (BOOL)textFieldShouldEndEditing:(UITextField *)textField{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y+80, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popToRootViewControllerAnimated:YES];
}

- (void)updateXx:(id)sender
{
    [self.textView resignFirstResponder];
    [accountField resignFirstResponder];
    if([Helper isConnectionAvailable]){
        NSString *urlStr=[NSString stringWithFormat:@"%@%@?opinion=%@&contact=%@",[Helper getIp],opinion_url,[self.textView.text stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding],accountField.text];
        NSString *response =[QuHaoUtil requestDb:urlStr];
        if([response isEqualToString:@""]){
            //异常处理
            [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
        }else{
            if([response isEqualToString:@"success"]){
                //解析错误
                [Helper showHUD2:@"提交成功" andView:self.view andSize:100];
            }else{
                [Helper showHUD2:@"服务器错误" andView:self.view andSize:100];
            }
        }
    }else{
        [Helper showHUD2:@"当前网络不可用" andView:self.view andSize:100];
    }
    
}

@end