//
//  RegisterController.h
//  quHaoIos
//
//  Created by sam on 13-10-27.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Helper.h"
#import "ASIHTTPRequest.h"
#import "ApiError.h"
#import "ASIFormDataRequest.h"
#import "MineViewController.h"
@interface RegisterController : UIViewController<UITextFieldDelegate>
@property (strong,nonatomic) UITextField *accountField;
@property (strong,nonatomic) UITextField *mdField;
@property (strong,nonatomic) UITextField *passField;

@end
