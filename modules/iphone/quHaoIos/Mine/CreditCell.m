//
//  CreditCell.m
//  quHaoIos
//
//  Created by sam on 13-11-24.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "CreditCell.h"
#import "Helper.h"
@implementation CreditCell
@synthesize creditModel;

-(id) initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self=[super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if(self){
        [self initSubviews];
        self.accessoryType=UITableViewCellAccessoryDetailDisclosureButton;
    }
    return self;
}

-(void)initSubviews
{
      
    _titleLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _titleLabel.frame=CGRectMake(5, 5, 300, 30);
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.textColor=[UIColor blackColor];
    _titleLabel.font=[UIFont fontWithName:@"AppleGothic" size:17.0];
    [self.contentView addSubview:_titleLabel];
    
    _pjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _pjLabel.frame=CGRectMake(10,40, 140, 30);
    _pjLabel.backgroundColor=[UIColor clearColor];
    _pjLabel.font=[UIFont fontWithName:@"CourierNewPSMT" size:13.0];
    [self.contentView addSubview:_pjLabel];
    
    _timeLabel=[[UILabel alloc] initWithFrame:CGRectZero];
    _timeLabel.font=[UIFont fontWithName:@"CourierNewPSMT" size:13.0];
    _timeLabel.frame=CGRectMake(150, _pjLabel.frame.origin.y, 160, 30);
    _timeLabel.backgroundColor=[UIColor whiteColor];
    _timeLabel.textAlignment=NSTextAlignmentRight;
    [self.contentView addSubview:_timeLabel];
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    _titleLabel.text=self.creditModel.merchantName;
    if (![self.creditModel.status isEqualToString:@""]) {
        if ([creditModel.status isEqualToString:@"finished"]) {
            _pjLabel.text=[NSString stringWithFormat:@"%@%d%@",@"完成取号，获得",creditModel.jifen,@"积分"];
        }else if ([creditModel.status isEqualToString:@"getNumber"]) {
            _pjLabel.text=[NSString stringWithFormat:@"%@%d%@",@"取号，减少",creditModel.jifen,@"积分"];
        }else if ([creditModel.status isEqualToString:@"exchange"]) {
            _pjLabel.text=[NSString stringWithFormat:@"%@%d%@",@"签到满5次，获得",creditModel.jifen,@"积分"];
        }else if ([creditModel.status isEqualToString:@"comment"]) {
            _pjLabel.text=[NSString stringWithFormat:@"%@%d%@",@"评价，获得",creditModel.jifen,@"积分"];
        }

        _timeLabel.text=[Helper formatDate:creditModel.created];
    }else{
        _pjLabel.text=@"";

    }
}
@end