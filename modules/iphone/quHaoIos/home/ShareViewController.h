//
//  ShareViewController.h
//  quHaoIos
//
//  Created by sam on 14-2-2.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Helper.h"
#import <QuartzCore/CALayer.h>
#import "QuHaoUtil.h"
#import "OAuthWebViewController.h"
#import "MBProgressHUD.h"

@interface ShareViewController : UIViewController<UITextViewDelegate,MBProgressHUDDelegate>
{
    MBProgressHUD *_HUD;
}
@property (nonatomic, strong) UITextView *textView;
@property (strong,nonatomic) NSString * mname;

- (void)shareWb:(id)sender;

@end
