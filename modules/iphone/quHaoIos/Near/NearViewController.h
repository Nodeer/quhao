//
//  NearViewController.h
//  quHaoApp
//
//  Created by sam on 13-7-28.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "BaseSearchViewController.h"
#import "Helper.h"
#import "NearCell.h"
#import "MerchartModel.h"
#import "MerchartDetail.h"
@interface NearViewController :BaseSearchViewController
{
    @private
    NSMutableArray *_merchartsArray;
    BOOL _reloading;
    int prevItemCount;
    //上拉刷新用的页码
    int pageIndex;
    
    //上拉刷新的view
    MJRefreshFooterView *_footer;
}
@end
