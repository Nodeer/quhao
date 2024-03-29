//
//  HistoryCell.m
//  quHaoIos
//
//  Created by sam on 13-11-10.
//  Copyright (c) 2013年 sam. All rights reserved.
//

#import "HistoryCell.h"

@implementation HistoryCell

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
    UIImage *image= [UIImage   imageNamed:@"arrow_right.png"];
    button = [UIButton buttonWithType:UIButtonTypeCustom];
    CGRect frame = CGRectMake(0.0, 0.0, image.size.width, image.size.height);
    button.frame = frame;
    [button setBackgroundImage:image forState:UIControlStateNormal];
    button.backgroundColor= [UIColor clearColor];
    
    self.egoImgView = [[EGOImageView alloc] initWithPlaceholderImage:[UIImage imageNamed:@"no_logo.png"]];
    self.egoImgView.frame = CGRectMake(5, 10, 105, 80);
    UITapGestureRecognizer *Tap =[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(imagePressed:)];
    [Tap setNumberOfTapsRequired:1];
    [Tap setNumberOfTouchesRequired:1];
    self.egoImgView.userInteractionEnabled=YES;
    [self.egoImgView addGestureRecognizer:Tap];
    [self.contentView addSubview:self.egoImgView];
    
    _titleLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _titleLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 10, kDeviceWidth-150, 30);
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.textColor=[UIColor blackColor];
    _titleLabel.font=[UIFont boldSystemFontOfSize:18];
    [self.contentView addSubview:_titleLabel];
    
    _pjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _pjLabel.backgroundColor=[UIColor clearColor];
    _pjLabel.textColor=[UIColor grayColor];
    [self.contentView addSubview:_pjLabel];
    _pjLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+10,45, 200, 30);
    
    _timeLabel=[[UILabel alloc] initWithFrame:CGRectZero];
    _timeLabel.font=[UIFont fontWithName:@"CourierNewPSMT" size:13.0];
    _timeLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 70, 160, 30);
    _timeLabel.backgroundColor=[UIColor whiteColor];
    _timeLabel.textAlignment=NSTextAlignmentLeft;
    [self.contentView addSubview:_timeLabel];
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    if ([[Helper returnUserString:@"showImage"] boolValue]&&![self.reservationModel.imgUrl isEqualToString:@""])
    {
        self.egoImgView.imageURL = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@",IP,[self.reservationModel.imgUrl  stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding ]]];
    }else{
        self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
    }
    self.egoImgView.id=self.reservationModel.merchantId;
    _titleLabel.text=self.reservationModel.name;
    
    _pjLabel.font = [UIFont systemFontOfSize:15];
    if([self.reservationModel.status isEqualToString:@"canceled"]){
        _pjLabel.text = @"取消记录不能评价";
        _pjLabel.textColor = [UIColor grayColor];
    }else{
        if (!self.reservationModel.isCommented) {
            _pjLabel.text = @"待评价";
            _pjLabel.textColor = [UIColor blackColor];
        }else{
            _pjLabel.text = @"已评价";
            _pjLabel.textColor = [UIColor grayColor];
        }
    }
    
    _timeLabel.text=[Helper formatDate:self.reservationModel.created];
    
    if (![self.reservationModel.status isEqualToString:@"canceled"])
    {
        self.accessoryView = button;
    }else{
        self.accessoryView = nil;
        self.accessoryType = UITableViewCellAccessoryNone;
    }
}

- (void)imagePressed:(UITapGestureRecognizer *)sender
{
    UITapGestureRecognizer *tap = (UITapGestureRecognizer*)sender;
    EGOImageView *image=(EGOImageView*)tap.view;
    if(NULL == image.id || [image.id isEqualToString:@""]){
        return;
    }
    
    MerchartDetail *mDetail = [[MerchartDetail alloc] init];
    mDetail.merchartID = image.id;
    mDetail.tabBarItem.image = [UIImage imageNamed:@"detail"];
    mDetail.hidesBottomBarWhenPushed=YES;
    [self.viewController.navigationController pushViewController:mDetail animated:YES];
}

-(void)dealloc
{
    self.egoImgView=nil;
    _titleLabel=nil;
    _pjLabel=nil;
}
@end