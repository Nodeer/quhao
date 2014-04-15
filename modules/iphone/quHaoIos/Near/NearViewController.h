//
//  NearViewController.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//
#import <QuartzCore/QuartzCore.h>
#import "BaseSearchViewController.h"
#import "Helper.h"
#import "NearCell.h"
#import "MerchartModel.h"
#import "MerchartDetail.h"
#import "selectListView.h"

@interface NearViewController :BaseSearchViewController<UITableViewDelegate,UITableViewDataSource,selectListViewDelegate,MBProgressHUDDelegate>
{
    @private
    NSMutableArray *_merchartsArray;
    BOOL _reloading;
    int _prevItemCount;
    //上拉刷新用的页码
    int _pageIndex;
    BOOL _isOpinion;
    //上拉刷新的view
    MJRefreshFooterView *_footer;
    NSArray *arryList;
    NSArray *arryValueList;
    selectListView * _selectList;
    UIButton * _button;
    //是否弹出下拉列表
    int _showList;
    //设置搜索距离
    NSInteger * _dis;
    MBProgressHUD *_HUD;
    CLLocationDegrees _latitude;
	CLLocationDegrees _longitude;
    int _isMapLoading;
    BOOL _isLoading;
}
@property (strong, nonatomic)  UITableView *tableView;

-(NSString *)getMerchart:(NSString *)poiId;
//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController;
@end
