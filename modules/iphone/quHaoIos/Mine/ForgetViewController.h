//
//  ForgetViewController.h
//  quHaoIos
//
//  Created by sam on 13-10-29.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ASIHTTPRequest.h"
#import "Helper.h"
#import "UIScrollView+ScrollViewCategory.h"
#import "QuHaoUtil.h"
@interface ForgetViewController  : UIViewController<UITextFieldDelegate>

@property (strong, nonatomic) IBOutlet UITextField *accountField;
@property (strong, nonatomic) IBOutlet UITextField *mdField;
@property (strong, nonatomic) IBOutlet UITextField *passField;
@property (strong, nonatomic) IBOutlet UITextField *confirmPassField;
//获取验证码
- (IBAction)hqCode:(id)sender;

@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;
@end
