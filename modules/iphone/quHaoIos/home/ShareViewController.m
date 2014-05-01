//
//  ShareViewController.m
//  quHaoIos
//
//  Created by sam on 14-2-2.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "ShareViewController.h"

@interface ShareViewController ()

@end

@implementation ShareViewController
@synthesize mname;
@synthesize textView;
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"分享微博", @"分享微博");
        self.tabBarItem.title = @"分享微博";
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.view.backgroundColor = [UIColor whiteColor];
    UIButton *backButton=[Helper getBackBtn:@"back.png" title:@" 返 回" rect:CGRectMake( 0, 5, 50, 30 )];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    UIButton *btnButton=[Helper getBackBtn:@"button.png" title:@" 分 享" rect:CGRectMake( 0, 0, 40, 25 )];
    [btnButton addTarget:self action:@selector(shareWb:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *buttonItem = [[UIBarButtonItem alloc] initWithCustomView:btnButton];
    self.navigationItem.rightBarButtonItem = buttonItem;
    
    self.textView = [[UITextView alloc] initWithFrame:CGRectMake(5, 10, kDeviceWidth-10, 150)];
    self.textView.layer.borderColor = UIColor.grayColor.CGColor;
    self.textView.layer.borderWidth = 1;
    self.textView.layer.cornerRadius = 6.0;
    self.textView.layer.masksToBounds = YES;
    self.textView.clipsToBounds = YES;
    self.textView.font = [UIFont fontWithName:@"Arial" size:16.0];//设置字体名字和字体大小
    self.textView.delegate = self;//设置它的委托方法
    self.textView.backgroundColor = [UIColor whiteColor];//设置它的背景颜色
    self.textView.text = [NSString stringWithFormat:@"%@%@%@",@"##取号##发现这个软件不错哦!在'",self.mname,@"'手机直接拿号不用排队,快去看看吧。@取号啦"];//设置它显示的内容
    self.textView.returnKeyType = UIReturnKeyDefault;//返回键的类型
    self.textView.keyboardType = UIKeyboardTypeDefault;//键盘类型
    self.textView.scrollEnabled = YES;//是否可以拖动
    self.textView.autoresizingMask = UIViewAutoresizingFlexibleHeight;//自适应高度
    [self.view addSubview: self.textView];
}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    [self.textView resignFirstResponder];
}

#pragma mark - 调整输入框与关闭键盘  TextView
- (BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (BOOL)textViewShouldEndEditing:(UITextView *)textView
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y, self.view.frame.size.width, self.view.frame.size.height);
    [UIView commitAnimations];
    return YES;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)shareWb:(id)sender
{
    BOOL authValid = QuHaoUtil.isAuthValid;
    
    if (!authValid)
    {
        OAuthWebViewController *auth = [[OAuthWebViewController alloc] init];
        auth.hidesBottomBarWhenPushed=YES;
        [self.navigationController pushViewController:auth animated:YES];
    }
    else
    {
        [self.textView becomeFirstResponder];
        [self sendWeibo];
    }
}

- (void)hudWasHidden:(MBProgressHUD *)hud
{
    [_HUD removeFromSuperview];
	_HUD = nil;
}

-(void)createHud
{
    _HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:_HUD];
    _HUD.mode = MBProgressHUDModeIndeterminate;
    _HUD.labelText = @"正在发送...";
    [_HUD show:YES];
    _HUD.delegate = self;
}

- (void)sendWeibo {
    
    [self.textView resignFirstResponder];
    
    NSString *content = [[NSString alloc] initWithString:self.textView.text];
    //计算发送微博的内容字数,并作相应的处理
    NSInteger contentLength = content.length;
    if (contentLength == 0) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil message:@"请输入微博内容！" delegate:nil cancelButtonTitle:@"好" otherButtonTitles:nil, nil];
        [alert show];
    }
    else if (contentLength > 140) {
        MBProgressHUD *overLengthHud = [[MBProgressHUD alloc] initWithView:self.view];
        [self.view addSubview:overLengthHud];
        //overLengthHud.mode = MBProgressHUDModeText;
        overLengthHud.labelText = @"提示信息";
        overLengthHud.detailsLabelText = [NSString stringWithFormat:@"微博字数:%ld 超过140上限！",(long)contentLength];
        [overLengthHud show:YES];
        [overLengthHud hide:YES afterDelay:1];
    }
    else {
        [self createHud];
        if([Helper isConnectionAvailable]){
            [self postWithText:content];
        }else{
            _HUD.labelText =@"当前网络不可用";
        }
    }
}

//发布文字图片微博
-(void)postWithText:(NSString *)text{
        NSURL *url = [NSURL URLWithString:WEIBO_UPDATE];
        ASIFormDataRequest *item = [[ASIFormDataRequest alloc] initWithURL:url];
        [item setPostValue:[Helper returnUserString:@"access_token"] forKey:@"access_token"];
        [item setPostValue:self.textView.text        forKey:@"status"];
        [item setDelegate:self];
        [item setDidFailSelector:@selector(requestFailed:)];
        [item setDidFinishSelector:@selector(requestLogin:)];
        [item startAsynchronous];
}

- (void)requestFailed:(ASIHTTPRequest *)requestNew
{
    if (_HUD!=nil) {
        [_HUD hide:YES];
    }
}

- (void)requestLogin:(ASIHTTPRequest *)requestNew
{
    _HUD.customView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"37x-Checkmark.png"]];
    _HUD.labelText = @"发微博成功！";
    _HUD.mode = MBProgressHUDModeCustomView;
    [_HUD hide:YES];
    [self performSelector:@selector(clickToHome:) withObject:nil afterDelay:1.0f];
}

@end