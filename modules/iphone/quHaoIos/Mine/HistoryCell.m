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
    self.egoImgView = [[EGOImageView alloc] initWithFrame:CGRectMake(5, 10, 105, 80)];
    self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
    [self.contentView addSubview:self.egoImgView];
    
    _titleLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    _titleLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+5, 10, 200, 30);
    _titleLabel.backgroundColor=[UIColor clearColor];
    _titleLabel.textColor=[UIColor blackColor];
    _titleLabel.font=[UIFont boldSystemFontOfSize:18];
    [self.contentView addSubview:_titleLabel];
    
    pjLabel=[[UILabel alloc]initWithFrame:CGRectZero];
    pjLabel.backgroundColor=[UIColor clearColor];
    pjLabel.textColor=[UIColor grayColor];
    [self.contentView addSubview:pjLabel];
    pjLabel.frame=CGRectMake(self.egoImgView.frame.origin.x+self.egoImgView.frame.size.width+10,45, 200, 30);
}

-(void) setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];
}

-(void)layoutSubviews
{
    [super layoutSubviews];
    if (![[Helper returnUserString:@"showImage"] boolValue]||[self.reservationModel.imgUrl isEqualToString:@""])
    {
        self.egoImgView.image = [UIImage imageNamed:@"no_logo.png"];
    }
    else
    {
        self.egoImgView.imageURL = [NSURL URLWithString:self.reservationModel.imgUrl];
    }
    
    _titleLabel.text=self.reservationModel.name;
    
    if (!self.reservationModel.isCommented) {
        pjLabel.text=@"待评价";
    }else{
        pjLabel.text=@"";
    }
}
-(void)dealloc
{
    self.egoImgView=nil;
    _titleLabel=nil;
    pjLabel=nil;
}
@end