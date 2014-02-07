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

@interface ShareViewController : UIViewController<UITextViewDelegate>

@property (nonatomic, strong) UITextView *textView;

- (void)shareWb:(id)sender;

@end
