//
//  MerchartHistoryController.h
//  quHaoIos
//
//  Created by sam on 13-10-22.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MJRefreshFooterView.h"
#import "MerchartDetail.h"
#import "HistoryCell.h"
#import "AppraiseViewController.h"
@interface MerchartHistoryController : UITableViewController
{
@private
    NSMutableArray *_reservationArray;
    BOOL _reloading;
    BOOL loadFlag;
    int prevItemCount;
    //上拉刷新用的页码
    int pageIndex;
    //上拉刷新的view
    MJRefreshFooterView *_footer;
}

@property (strong,nonatomic) NSString * accouId;
//加载页面上的导航
-(void)loadNavigationItem;
//弹出历史信息页面
- (void)pushHistoryDetail:(Reservation *)model andNavController:(UINavigationController *)navController;

//请求服务端获取数据
-(void)requestData:(NSString *)urlStr withPage:(int)page;
@end


