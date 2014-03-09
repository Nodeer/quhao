//
//  HomeViewController.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MJRefreshFooterView.h"
#import "MJRefreshHeaderView.h"
@interface ListViewController : UITableViewController
{
    @private
    NSMutableArray *_merchartsArray;
    BOOL _reloading;
    BOOL _loadFlag;
    int _prevItemCount;
    //上拉刷新用的页码
    int _pageIndex;
    
    //下拉刷新的view
    MJRefreshHeaderView *_header;
    //上拉刷新的view
    MJRefreshFooterView *_footer;
    //判断是上拉还是下拉 1 上拉 2下拉
    int _whichView;
}

@property (strong,nonatomic) NSString * currentDateStr;
@property (strong,nonatomic) NSString * cateType;

//加载页面上的导航
-(void)loadNavigationItem;
//返回主页
- (void)clickToHome:(id)sender;
//搜索的点击事件
- (void)clickSearch:(id)sender;
//弹出商家详细页面
- (void)pushMerchartDetail:(MerchartModel *)model andNavController:(UINavigationController *)navController andIsNextPage:(BOOL)isNextPage;
//请求服务端获取数据
-(void)requestData:(NSString *)urlStr withPage:(int)page;

@end
