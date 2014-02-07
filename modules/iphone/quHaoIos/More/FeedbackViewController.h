//
//  FeedbackViewController.h
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/CALayer.h>
#import "Helper.h"
#import "QuHaoUtil.h"
@interface FeedbackViewController   : UIViewController<UITextFieldDelegate,UITextViewDelegate>

@property (strong,nonatomic) UITextField *accountField;
@property (nonatomic, strong) UITextView *textView;

- (void)updateXx:(id)sender;

@end
