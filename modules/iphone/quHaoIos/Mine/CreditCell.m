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
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.textColor=[UIColor blackColor];
    _titleLabel.font=[UIFont fontWithName:@"AppleGothic" size:17.0];
    [self.contentView addSubview:_titleLabel];
    
    pjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    pjLabel.backgroundColor=[UIColor clearColor];
    pjLabel.font=[UIFont fontWithName:@"CourierNewPSMT" size:13.0];
    [self.contentView addSubview:pjLabel];
    
    timeLabel=[[UILabel alloc] initWithFrame:CGRectZero];
    timeLabel.font=[UIFont fontWithName:@"CourierNewPSMT" size:13.0];
    [self.contentView addSubview:timeLabel];
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
        
    _titleLabel.frame=CGRectMake(5, 5, 300, 30);
    _titleLabel.text=self.creditModel.merchantName;
    NSString * credit=@"";
    if (![self.creditModel.status isEqualToString:@""]) {
        pjLabel.frame=CGRectMake(10,40, 140, 30);
        if ([creditModel.status isEqualToString:@"finished"]) {
            credit=[NSString stringWithFormat:@"%@%d%@",@"完成取号，获得",creditModel.jifen,@"积分"];
        }else if ([creditModel.status isEqualToString:@"getNumber"]) {
            credit=[NSString stringWithFormat:@"%@%d%@",@"取号，减少",creditModel.jifen,@"积分"];
        }else if ([creditModel.status isEqualToString:@"exchange"]) {
            credit=[NSString stringWithFormat:@"%@%d%@",@"签到满5次，获得",creditModel.jifen,@"积分"];
        }
        pjLabel.text=credit;

        timeLabel.frame=CGRectMake(150, pjLabel.frame.origin.y, 160, 30);
        timeLabel.text=[Helper formatDate:creditModel.created];
        timeLabel.backgroundColor=[UIColor whiteColor];
        timeLabel.textAlignment=NSTextAlignmentRight;
    }
}
@end