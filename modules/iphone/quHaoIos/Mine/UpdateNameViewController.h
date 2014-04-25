//
//  UpdateNameViewController.h
//  quHaoIos
//
//  Created by sam on 14-4-15.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "Helper.h"
#import "QuHaoUtil.h"
@interface UpdateNameViewController : UIViewController<UITextViewDelegate,UITextFieldDelegate>
{
    UITextField *_accountField;
}
@property (strong,nonatomic) NSString *name;
@property (strong,nonatomic) NSString *aid;
@property (nonatomic, assign) id delegate;

@end

@protocol UpdateNameViewDelegate
- (void) nameUpdate:(NSString*)username;
@end
