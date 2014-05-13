//
//  ProtocolViewController.m
//  quHaoIos
//
//  Created by sam on 14-5-12.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import "ProtocolViewController.h"

@interface ProtocolViewController ()

@end

@implementation ProtocolViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"用户协议";
    
    UIButton *backButton=[Helper getBackBtn:@"back"];
    [backButton addTarget:self action:@selector(clickToHome:) forControlEvents:UIControlEventTouchUpInside];
    UIBarButtonItem *backButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backButton];
    self.navigationItem.leftBarButtonItem = backButtonItem;
    
    self.view.backgroundColor  = [ UIColor whiteColor];
    UITextView *textView = [[UITextView alloc] initWithFrame:CGRectMake(10, 0, kDeviceWidth-20, kDeviceHeight-64)];
    [self.view addSubview:textView];
    
    [self createHud];
    [self requestData];
    textView.textColor = [UIColor darkGrayColor];
    textView.font = [UIFont fontWithName:@"Arial" size:13.0];
    textView.text = _content;
    [textView setEditable:NO];
    [_HUD hide:YES];
}

-(void)requestData
{
    if ([Helper isConnectionAvailable])
    {
        NSString *response =[QuHaoUtil requestDb:[NSString stringWithFormat:@"%@%@",IP,getUserAgreement]];
        if([response isEqualToString:@"false"]){
            //异常处理
            _HUD.labelText = @"服务器错误";
        }else{
            NSDictionary *dic = [QuHaoUtil analyseDataToDic:response];
            _content = [dic objectForKey:@"content"];
        }
    }else
    {
        _HUD.labelText = @"当前网络不可用";
    }
}

#pragma mark HUD
- (void)hudWasHidden:(MBProgressHUD *)hud {
    [_HUD removeFromSuperview];
	_HUD = nil;
}

-(void)createHud
{
    _HUD = [[MBProgressHUD alloc] initWithView:self.view];
    [self.view addSubview:_HUD];
    _HUD.mode = MBProgressHUDModeIndeterminate;
    _HUD.labelText = @"正在加载";
    [_HUD show:YES];
    _HUD.delegate = self;
}

- (void)clickToHome:(id)sender
{
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
@end
