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
@interface QuHaoViewController : UIViewController<UITableViewDataSource,UITableViewDelegate,UIPickerViewDelegate,UIPickerViewDataSource>
{
    UITableView *_quHaoView;
    UIPickerView *selectView;//下拉列表
    NSArray *pickerData;
    BOOL showList;//是否弹出下拉列表

}

@property (strong,nonatomic) NSString * merchartID;
@property (strong,nonatomic) NSString * accountID;
@property (strong,nonatomic) Reservation * reservation;
@property (strong,nonatomic) NSArray *seatType;

//加载用户的座位信息
-(void)reloadReversion;
//拿号的请求方法
-(void)reloadView;
//下拉列表的初始化
-(void)dropdown;
//加载用户的座位的信息
-(void)reloadCurrent;
@end
