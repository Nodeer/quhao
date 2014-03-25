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
@interface RegisterViewController : UIViewController<UITextFieldDelegate>

@property (strong, nonatomic) IBOutlet UITextField *mdField;
@property (strong, nonatomic) IBOutlet UITextField *accountField;
@property (strong, nonatomic) IBOutlet UITextField *passField;
@property (strong, nonatomic) IBOutlet UITextField *confirmPass;
- (IBAction)hqCode:(id)sender;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;

@end
