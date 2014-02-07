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
@interface ForgetViewController  : UIViewController<UITextFieldDelegate>
@property (strong,nonatomic) UITextField *accountField;
@property (strong,nonatomic) UITextField *mdField;
@property (strong,nonatomic) UITextField *passField;
@property (strong,nonatomic) UITextField *confirmPassField;

@end
