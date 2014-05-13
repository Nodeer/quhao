//
//  NearCell.h
//  quHaoIos
//
//  Created by sam on 14-2-5.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EGOImageView.h"
#import "Helper.h"
#import "MerchartModel.h"
@class MerchartModel;
@interface NearCell : UITableViewCell
{
@private
    UILabel *_titleLabel;
    UILabel *_disLabel;
    UIImageView *_quhao;
    UILabel *_rjLabel;
    MerchartModel *_merchartModel;
    UIImageView *_youhui;
}

@property(nonatomic,strong) MerchartModel *merchartModel;
@property (strong,nonatomic) EGOImageView * egoImgView;

@end;