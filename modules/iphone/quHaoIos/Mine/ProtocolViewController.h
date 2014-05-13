//
//  ProtocolViewController.h
//  quHaoIos
//
//  Created by sam on 14-5-12.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Helper.h"
#import "QuHaoUtil.h"
#import "MBProgressHUD.h"
@interface ProtocolViewController : UIViewController<MBProgressHUDDelegate>
{
    MBProgressHUD *_HUD;
    NSString *_content;
}

@end
