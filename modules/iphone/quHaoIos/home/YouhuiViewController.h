//
//  YouhuiViewController.h
//  quHaoIos
//
//  Created by sam on 14-5-12.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Helper.h"
#import "QuHaoUtil.h"
#import "MBProgressHUD.h"
@interface YouhuiViewController : UIViewController<MBProgressHUDDelegate>
{
    NSMutableArray *_titles;
    NSMutableArray *_contexts;
    MBProgressHUD *_HUD;
    UIScrollView * _sc;
}
@property (strong,nonatomic) NSString * mid;

@end
