//
//  OAuthWebViewController.h
//  quHaoIos
//
//  Created by sam on 14-2-1.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "WeiboSDK.h"
#import "QuHaoUtil.h"
#import "MBProgressHUD.h"
#import "Helper.h"
@interface OAuthWebViewController : UIViewController<UIWebViewDelegate,WBHttpRequestDelegate>

@property (nonatomic, strong)  UIWebView *webView;

@end
