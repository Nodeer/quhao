//
//  CurrentCell.h
//  quHaoIos
//
//  Created by sam on 14-3-13.
//  Copyright (c) 2014年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "EGOImageView.h"
#import "Helper.h"
#import "QuHaoUtil.h"
@class MerchartModel;
@interface CurrentCell : UITableViewCell
{
@private
    UILabel *_titleLabel;
    UIButton *_cancelButton;
    MerchartModel *_merchartModel;
}

@property(nonatomic,strong) MerchartModel *merchartModel;
@property (strong,nonatomic) EGOImageView * egoImgView;

@end
