//
//  MineInfoViewController.h
//  quHaoIos
//
//  Created by sam on 14-4-15.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "SettingModel.h"
#import "Helper.h"
#import "UpdatePassViewController.h"
#import "JFViewController.h"
#import "UpdateNameViewController.h"
#import "CreditViewController.h"
@interface MineInfoViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UpdateNameViewDelegate,UIAlertViewDelegate>

@property (strong,nonatomic) NSString *name;
@property (strong,nonatomic) NSString *accountId;
@property  int jifen;
@property (strong,nonatomic) NSArray * settings;
@property (strong,nonatomic) NSMutableDictionary * settingsInSection;
@property (strong,nonatomic) UITableView *tview;

@end
