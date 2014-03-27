//
//  QuHaoViewController.h
//  quHaoIos
//
//  Created by sam on 13-9-22.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DbService.h"
#import "Helper.h"
#import "ASIHTTPRequest.h"
#import "SBJson.h"
#import "Reservation.h"
#import "QuHaoUtil.h"
@interface QuHaoViewController : UIViewController<UITableViewDataSource,UITableViewDelegate>
{
    BOOL _showList;//是否弹出下拉列表
    UILabel *_currlabel;
    UILabel *_seatNumber;
    UIButton *_nahaoBtn;
}

@property (strong,nonatomic) NSString * merchartID;
@property (strong,nonatomic) NSString * accountID;
@property (strong,nonatomic) Reservation * reservation;
@property (strong,nonatomic) NSArray *seatType;
@property (nonatomic,retain) UITableView *popView;
@property (nonatomic, retain) UIView * coverView;//黑色半透明遮盖层

//加载用户的座位信息
-(void)reloadReversion;
//拿号的请求方法
-(void)reloadView;
//加载用户的座位的信息
-(void)reloadCurrent;
@end
