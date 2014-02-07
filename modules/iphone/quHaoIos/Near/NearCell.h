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
    UILabel *_statusLabel;
    MerchartModel *_merchartModel;
}

@property(nonatomic,strong) MerchartModel *merchartModel;
@property (strong,nonatomic) EGOImageView * egoImgView;

@end;