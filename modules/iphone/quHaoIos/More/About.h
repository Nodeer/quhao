//
//  About.h
//  quHaoApp
//
//  Created by sam on 13-9-30.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <UIKit/UIKit.h>
#import "Helper.h"
#import "QuHaoUtil.h"
@interface About : UIViewController<UITableViewDelegate,UITableViewDataSource>
{
    NSString * _version;
    NSArray * _arrayList;
}

@end
