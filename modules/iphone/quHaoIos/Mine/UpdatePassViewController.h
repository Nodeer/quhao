//
//  UpdatePassViewController.h
//  quHaoIos
//
//  Created by sam on 14-4-15.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "Helper.h"
#import "QuHaoUtil.h"
@interface UpdatePassViewController : UIViewController
{
    UITextField *_currentField;
    UITextField *_newField;
    UITextField *_comfirmField;
}
@property (strong,nonatomic) NSString *aid;

@end
