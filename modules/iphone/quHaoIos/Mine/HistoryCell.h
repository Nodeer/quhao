//
//  HistoryCell.h
//  quHaoIos
//
//  Created by sam on 13-11-10.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MerchartModel.h"
#import "MerchartDetail.h"
#import "EGOImageView.h"
#import "UIView+EventLink.h"
@interface HistoryCell : UITableViewCell
{
    UILabel *_titleLabel;
    UILabel *_pjLabel;
    UILabel *_timeLabel;

    Reservation *_reservationModel;
    UIButton *button;
    
}
@property(nonatomic,strong) Reservation *reservationModel;
@property (strong,nonatomic) EGOImageView * egoImgView;

@end
