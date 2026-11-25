-- Chạy đúng một lần trên CSDL đã được khởi tạo bằng schema.sql
-- trước khi bổ sung hai sự kiện audit cho tài khoản.
-- CSDL mới: schema.sql đã có sẵn hai mã này, không cần chạy migration.

ALTER TABLE nhat_ky
    DROP CHECK chk_nky_thao_tac;

ALTER TABLE nhat_ky
    ADD CONSTRAINT chk_nky_thao_tac CHECK (thao_tac IN (
        'LAP_PHIEU_THU', 'HUY_PHIEU_THU', 'SUA_SO_TIEN_PHAI_NOP',
        'TAO_KHOAN_TU_NGUYEN', 'SUA_DON_GIA', 'CHOT_CONG_NO',
        'THEM_HO', 'SUA_HO', 'HO_CHUYEN_DI',
        'THEM_NHAN_KHAU', 'SUA_NHAN_KHAU', 'NHAN_KHAU_CHUYEN_DI',
        'DOI_CHU_HO', 'GHI_TAM_TRU_TAM_VANG',
        'TAO_TAI_KHOAN', 'DOI_VAI_TRO', 'KHOA_TAI_KHOAN',
        'MO_KHOA_TAI_KHOAN', 'DAT_LAI_MAT_KHAU',
        'SUA_THONG_TIN_TAI_KHOAN', 'DOI_MAT_KHAU'
    ));
