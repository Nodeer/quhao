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
#import "QuHaoUtil.h"
#import "CommentCell.h"
#import "Helper.h"
@interface CommentViewController : UITableViewController<MBProgressHUDDelegate>
{
@private
    NSMutableArray *_commentsArray;
    CGFloat _cellHeight;
    NSString *_url;
    MBProgressHUD *_HUD;
    BOOL _isLoading;
    int _allCount;
    BOOL _isLoadOver;
    UIView *_tableFooterView;
    UILabel * _loadMoreText;
    UIActivityIndicatorView *_tableFooterActivityIndicator;
    RefreshState _state;
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
-(void)requestData;

@end

