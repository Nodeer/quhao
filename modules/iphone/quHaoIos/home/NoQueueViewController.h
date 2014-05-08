//
//  NoQueueViewController.h
//  quHaoIos
//
//  Created by sam on 14-4-28.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "Helper.h"
#import "NearCell.h"
#import "MerchartModel.h"
#import "MerchartDetail.h"
#import "selectListView.h"
#import "WGS84TOGCJ02.h"
@interface NoQueueViewController :UIViewController<UITableViewDelegate,UITableViewDataSource,selectListViewDelegate,MBProgressHUDDelegate,CLLocationManagerDelegate>
{
@private
    NSMutableArray *_merchartsArray;
    NSArray *arryList;
    NSArray *arryValueList;
    selectListView * _selectList;
    UIButton * _button;
    //是否弹出下拉列表
    int _showList;
    //设置搜索距离
    int  _dis;
    MBProgressHUD *_HUD;
    NSString * _latitude;
	NSString * _longitude;
    int _allCount;
    BOOL _isLoading;
    BOOL _isLoadOver;
    UIView *_tableFooterView;
    UILabel * _loadMoreText;
    UIActivityIndicatorView *_tableFooterActivityIndicator;
    PullRefreshState _state;
}
@property (strong, nonatomic)  UITableView *tableView;
@property (strong, nonatomic) CLLocationManager *locationManager;

//上拉刷新增加数据
-(NSMutableArray *)addAfterInfo:(NSArray *) objects;
-(void)createHud;
//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController;
@end