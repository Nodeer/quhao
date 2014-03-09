//
//  CreditViewController.h
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "ASIHTTPRequest.h"
#import "SBJson.h"
#import "CreditCell.h"
#import "Helper.h"
#import "MJRefreshFooterView.h"
#import "QuHaoUtil.h"
@interface CreditViewController : UITableViewController
{
@private
    NSMutableArray *_creditArray;
    BOOL _reloading;
    BOOL _loadFlag;
    int _prevItemCount;
    //上拉刷新用的页码
    int _pageIndex;
    //上拉刷新的view
    MJRefreshFooterView *_footer;
}

@property (retain,nonatomic) NSString * accouId;

//加载页面上的导航
-(void)loadNavigationItem;
//请求服务端获取数据
-(void)requestData:(NSString *)urlStr withPage:(int)page;

@end