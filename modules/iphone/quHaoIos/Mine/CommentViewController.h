//
//  CommentViewController.h
//  quHaoIos
//
//  Created by sam on 13-12-8.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "Helper.h"
#import "CommentModel.h"
#import "MJRefreshFooterView.h"
#import "QuHaoUtil.h"
#import "CommentCell.h"
@interface CommentViewController : UITableViewController
{
@private
    NSMutableArray *_commentsArray;
    BOOL _reloading;
    BOOL loadFlag;
    int _prevItemCount;
    //上拉刷新用的页码
    int _pageIndex;
    //上拉刷新的view
    MJRefreshFooterView *_footer;
    CGFloat _cellHeight;
    NSString *_url;
}

@property (strong,nonatomic) NSString * accountOrMerchantId;
//判断是显示商家所有评论还是个人的 1 商家 2个人
@property  int whichComment;

//加载页面上的导航
-(void)loadNavigationItem;
- (void)clickToHome:(id)sender;
//处理字符串中的空格
-(NSString *)returnFormatString:(NSString *)string;
//请求服务端获取数据
-(void)requestData:(NSString *)urlStr withPage:(int)page;

@end

