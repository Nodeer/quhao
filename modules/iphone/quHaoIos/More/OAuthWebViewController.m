//
//  OAuthWebViewController.m
//  quHaoIos
//
//  Created by sam on 14-2-1.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "OAuthWebViewController.h"

@interface OAuthWebViewController ()
@end

@implementation OAuthWebViewController
@synthesize webView;

- (void)viewDidLoad
{
    [super viewDidLoad];
    webView=[[UIWebView alloc] initWithFrame:CGRectMake(0, 0, kDeviceWidth, kDeviceHeight)];
    self.view.backgroundColor=[UIColor whiteColor];
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
        
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"access_token"] == nil) {
        _hud = [MBProgressHUD showHUDAddedTo:self.webView animated:YES];
        _hud.labelText = NSLocalizedString(@"正在加载授权页面...", nil);
        _hud.removeFromSuperViewOnHide = YES;
        NSString *oauthUrlString = [QuHaoUtil returnOAuthUrlString];
        NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:oauthUrlString]];
        [self.webView setDelegate:self];
        [self.webView loadRequest:request];
        [self.view addSubview:webView];
    }
}

- (void)webViewDidFinishLoad:(UIWebView *)webView
{
    [_hud hide:YES];
}

- (void)clickToHome:(id)sender
{
    [self.navigationController  popViewControllerAnimated:YES];
}
- (BOOL)webView:(UIWebView*)webView shouldStartLoadWithRequest:(NSURLRequest*)request navigationType:(UIWebViewNavigationType)navigationType {
    
    NSURL *backURL = [request URL];  //接受重定向的URL
    NSString *backURLString = [backURL absoluteString];
    //判断是否是授权调用返回的url
    if ([backURLString hasPrefix:@"https://api.weibo.com/oauth2/default.html?"]) {
        
        //找到”code=“的range
        NSRange rangeOne;
        rangeOne=[backURLString rangeOfString:@"code="];
        
        //根据他“code=”的range确定code参数的值的range
        NSRange range = NSMakeRange(rangeOne.length+rangeOne.location, backURLString.length-(rangeOne.length+rangeOne.location));
        //获取code值
        NSString *codeString = [backURLString substringWithRange:range];
        QuHaoUtil *infoForSina = [[QuHaoUtil alloc] init];
        [infoForSina getAccessToken:codeString];
        [Helper showHUD2:@"授权成功" andView:self.view andSize:100];
        //跳转到主界面
        [self.navigationController  popViewControllerAnimated:YES];
    }
    return YES;
}

@end
